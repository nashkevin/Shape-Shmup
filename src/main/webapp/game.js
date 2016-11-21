const INPUT_RATE = 20; // maximum number of inputs per second
var SPEED_CAP = 100;   // maximum speed a player can travel
var webSocket;
var username;
var playerAgentID;
var clientInput = {};  // represents the current input of the player
var messages = document.getElementById("messages");

// Contains game objects (e.g. player agents) drawn on the screen, indexed by UUID.
var gameEntities = {};

var canvas = document.getElementById("gameCanvas");

canvas.addEventListener("mousedown", startFiring);
document.addEventListener("mouseup", stopFiring);
canvas.addEventListener("mousemove", trackAngle);

var renderer = PIXI.autoDetectRenderer(getGameWidth(), getGameHeight(), {view: canvas});
renderer.backgroundColor = 0x272822;
renderer.autoResize = true;

var stage = new PIXI.Container();
var bg = new PIXI.Texture.fromImage("images/background.png");
var bgTile = new PIXI.extras.TilingSprite(bg, 1920, 1080);
bgTile.position.set(0, 0);
bgTile.tilePosition.set(0, 0);
stage.addChild(bgTile);

var pingStartTime;

var isSpeedDecaying = false;
var decaySpeedID;
var isAccelerating = false;
var accelerateID;

function joinGame() {
	// Ensures only one connection is open at a time
	if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
		addMessageToChat("WebSocket is already opened.");
		return;
	}
	// Create a new instance of the websocket
	var url = "ws://" + window.location.host + "/socket";
	webSocket = new WebSocket(url);

	// Binds functions to the listeners for the websocket.
	webSocket.onopen = function(e) {
		if (e.data === undefined){
			return;
		}
		addMessageToChat(e.data);
	};

	webSocket.onmessage = function(e) {
		try {
			var json = JSON.parse(e.data);
			parseJson(json);
		} catch (error) { // Display non-JSON messages to the chat area
			// if the server is responding to a ping request
			if (error instanceof SyntaxError) {
				// If input is invalid JSON, treat it as plain text.
				if (e.data === "PONG") {
					addMessageToChat(Date.now() - pingStartTime + " ms");
				} else {
					addMessageToChat(e.data);
				}
			} else {
				throw error;
			}
		}
	};

	webSocket.onclose = function(e) {
		addMessageToChat("Connection closed");
	};

	function completeConnection() {
		var state = webSocket.readyState;
		if (state === webSocket.CONNECTING) {
			setTimeout(completeConnection, 250);
		} else if (state === webSocket.OPEN) {
			// Once connection is established
			username = document.getElementById("username").value.trim();
			webSocket.send(JSON.stringify({ 'name': username }));

			document.getElementById("pregame").classList.add("hidden");
			document.getElementById("game").classList.remove("hidden");
			resize();
			sendFrameInput();
		} else {
			alert("The connection to the server was closed before it could be established.");
		}
	}
	completeConnection();
}

// Sends the client's input to the server. Runs each frame.
function sendFrameInput() {
	if (connectedToGame()) {
		// Schedule the next frame.
		setTimeout(sendFrameInput, 1000 / INPUT_RATE);

		// Send any input to the server.
		json = JSON.stringify(clientInput);
		if (json !== "{}") {
			webSocket.send(json);
		}

		if (!clientInput.isFiring) {
			delete clientInput.angle;
		}
	}
}

// Sends the value of the text input to the server
function sendChatMessage() {
	var text = document.getElementById("messageInput").value;
	document.getElementById("messageInput").value = "";
	if (text != "") {
		if (text.toLowerCase() === "/clear") {
			messages.innerHTML = "";
			messages.scrollTop = messages.scrollHeight;
		}
		pingStartTime = Date.now();
		webSocket.send(JSON.stringify({ "message": text }));
	}
	document.getElementById("gameCanvas").focus();
}

function closeSocket() {
	webSocket.close();
	window.location.reload();
}

function addMessageToChat(text) {
	messages.innerHTML += "<br/>" + text;
	messages.scrollTop = messages.scrollHeight;
}

function getGameHeight() {
	return window.innerHeight - document.getElementById("chat").clientHeight;
}

function getGameWidth() {
	return window.innerWidth;
}

function resize() {
	renderer.resize(getGameWidth(), getGameHeight());
	renderer.render(stage);
}
window.onresize = resize;

function connectedToGame() {
	return (typeof webSocket !== "undefined" && webSocket.readyState === webSocket.OPEN);
}

function parseJson(json) {
	if (json.pregame) {
		// Set the ID of the corresponding player agent on the server end
		// so this client knows which agent it is when updating the screen.
		playerAgentID = json.id;
	} else {
		updateStage(json);
	}
}

function updateStage(json) {
	var playerAgents = json.playerAgents;
	var npcAgents = json.npcAgents;
	var projectiles = json.projectiles;

	// Get the player agent corresponding to this client.
	var thisPlayer = null;
	for (var i=0; i<playerAgents.length; i++) {
		var agent = playerAgents[i];
		if (agent.id === playerAgentID) {
			thisPlayer = agent;
			break;
		}
	}
	if (thisPlayer === null) {
		return;
	}

	// Mark all game entities as invisible (in case they should be despawned).
	for (var id in gameEntities) {
	    gameEntities[id].visible = false;
	}

	// Iterate through player agents
	for (var i=0; i<playerAgents.length; i++) {
		setScreenCoordinates(playerAgents[i], thisPlayer);
		var player = drawPlayer(playerAgents[i]);
		// If the player was included in the JSON, they should remain visible.
		player.visible = true;
	}

	//TODO iterate over NPC agents and projectiles.

	renderer.render(stage);
}

/** Calculate the coordinates of the entity in relation to the canvas screen.
thisPlayer is used as the center of the screen. */
function setScreenCoordinates(entity, thisPlayer) {
	var x_offset = entity.x - thisPlayer.x;
	var y_offset = entity.y - thisPlayer.y;
	entity.screen_x = getGameWidth() / 2 + x_offset;
	entity.screen_y = getGameHeight() / 2 + y_offset;
}

// Key down listener
window.onkeydown = function(e) {
	// Ignore key events within text input
	var currentTag = e.target.tagName.toLowerCase();
	if (currentTag == "input" || currentTag == "textarea") {
		return;
	}

	if (connectedToGame()) {
		var code = e.keyCode ? e.keyCode : e.which;
		switch (code) {
			case 87: case 38:  // 'w' or up
				e.preventDefault();
				clientInput.up = true;
				delete clientInput.down;
				break;
			case 65: case 37: // 'a' or left
				e.preventDefault();
				clientInput.left = true;
				delete clientInput.right;
				break;
			case 83: case 40: // 's' or down
				e.preventDefault();
				clientInput.down = true;
				delete clientInput.up;
				break;
			case 68: case 39: // 'd' or right
				e.preventDefault();
				clientInput.right = true;
				delete clientInput.left;
				break;
			case 13:          // enter
				e.preventDefault();
				document.getElementById("messageInput").focus();
				stopAllMovement();
				break;
			case 191:         // forward slash
				e.preventDefault();
				document.getElementById("messageInput").focus();
				document.getElementById("messageInput").value = "/";
				stopAllMovement();
				break;
		}
	}
};

// Key up listener
window.onkeyup = function(e) {
	// Ignore key events within text input
	var currentTag = e.target.tagName.toLowerCase();
	if (currentTag == "input" || currentTag == "textarea") {
		return;
	}

	if (connectedToGame()) {
		var code = e.keyCode ? e.keyCode : e.which;
		switch (code) {
			case 87: case 38:  // 'w' or up
				e.preventDefault();
				delete clientInput.up;
				break;
			case 65: case 37: // 'a' or left
				e.preventDefault();
				delete clientInput.left;
				break;
			case 83: case 40: // 's' or down
				e.preventDefault();
				delete clientInput.down;
				break;
			case 68: case 39: // 'd' or right
				e.preventDefault();
				delete clientInput.right;
				break;
		}
	}
};

function stopAllMovement() {
	delete clientInput.up;
	delete clientInput.left;
	delete clientInput.down;
	delete clientInput.right;
}

// Action when the user fires a projectile by clicking with the mouse
function startFiring(e) {
	this.focus(); // Move focus to the game canvas
	if (e.button == 0 && connectedToGame()) {
		clientInput.isFiring = true;
		trackAngle(e);
	}
}

function stopFiring(e) {
	delete clientInput.isFiring;
	delete clientInput.angle;
}

function trackAngle(e) {
	if (connectedToGame()) {
		clientInput.angle = coordinateToAngle(e.clientX, e.clientY);
	}
}

/* Returns angle in radians with the following conventions
 *     N: -pi/2     *
 *     E:     0     *
 *     S:  pi/2     *
 *     W:    pi     */
function coordinateToAngle(x, y) {
	var x_origin = renderer.width / 2;
	var y_origin = renderer.height / 2;

	return Math.atan2(y - y_origin, x - x_origin);
}

function drawPlayer(playerObject) {
	if (!gameEntities[playerObject.id]) {
		createPlayer(playerObject);
	}
	return updatePlayer(playerObject);
}

function createPlayer(playerObject) {
	// Create the container, which will contain all parts of the player.
	var playerContainer = new PIXI.Container();

	// Create the shape, which will be used as the graphic for the sprite.
	var playerShape = new PIXI.Graphics();
	playerShape.lineStyle(4, 0x87B56C, 1)
	playerShape.beginFill(0xD6EAD5);
	playerShape.drawPolygon([
		0,  25,
		50, 50,
		50, 0,
		0,  25
	]);
	playerShape.endFill();

	// Create the sprite, which is the shape itself that represents the player.
	var playerSprite = new PIXI.Sprite(renderer.generateTexture(playerShape));
	playerSprite.anchor.set(2/3, 0.5);
	playerSprite.pivot.set(2/3, 0.5);
	playerSprite.position.set(0, 0);

	playerContainer.addChild(playerSprite);

	// Create the text, which will display the player's name.
	var playerName = new PIXI.Text(playerObject.name, {
		fontFamily: "Arial",
		fontSize: 12 + (16 - playerObject.name.length),
		align: "center",
		fill: "#F8F8F2",
		dropShadow: true,
		dropShadowColor: "#222222",
		dropShadowDistance: 5,
		stroke: "#131411",
		strokeThickness: 2
	});
	playerName.anchor.set(0.5, 0.5);
	playerName.position = playerSprite.position;
	playerName.position.y += 50;

	playerContainer.addChild(playerName);

	var containerX = 0;
	var containerY = 0;

	playerContainer.position.set(containerX, containerY);
	stage.addChild(playerContainer);

	gameEntities[playerObject.id] = playerContainer;
	return playerContainer;
}

function updatePlayer(playerObject) {
	var playerContainer = gameEntities[playerObject.id];
	playerContainer.position.set(playerObject.screen_x, playerObject.screen_y);
	return playerContainer;
}

window.onload = function() {
	document.getElementById("username").select();
}

document.oncontextmenu = function() {
	return false;
}

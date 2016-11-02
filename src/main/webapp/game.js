const INPUT_RATE = 20; // maximum number of inputs per second
var webSocket;
var clientInput = {};  // represents the current input of the player
var messages = document.getElementById("messages");

var canvas = document.getElementById("gameCanvas");

canvas.addEventListener("mousedown", startFiring);
document.addEventListener("mouseup", stopFiring);
canvas.addEventListener("mousemove", trackAngle);

var renderer = PIXI.autoDetectRenderer(getGameWidth(), getGameHeight(), {view: canvas});
renderer.backgroundColor = 0x272822;
renderer.autoResize = true;

var stage = new PIXI.Container();
renderer.render(stage);

//console.log(renderer instanceof PIXI.WebGLRenderer); // Successfully using WebGL?

function joinGame() {
	// Ensures only one connection is open at a time
	if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
		addMessageToChat("WebSocket is already opened.");
		return;
	}
	// Create a new instance of the websocket
	var url = "ws://" + window.location.host + "/socket";
	webSocket = new WebSocket(url);

	// Binds functions to the listeners for the websocket.
	webSocket.onopen = function(e) {
		if(e.data === undefined)
			return;

		addMessageToChat(e.data);
	};

	webSocket.onmessage = function(e) {
		addMessageToChat(e.data);
	};

	webSocket.onclose = function(e) {
		addMessageToChat("Connection closed");
	};

	function completeConnection() {
		var state = webSocket.readyState;
		if (state === webSocket.CONNECTING) {
			setTimeout(completeConnection, 250);
		} else if (state === webSocket.OPEN) {
			// Once connection is established.
			var username = document.getElementById("username").value;
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
	var text = document.getElementById("messageinput").value;
	document.getElementById("messageinput").value = "";
	if (text != "") {
		webSocket.send(JSON.stringify({ 'message': text }));
	}
}

function closeSocket() {
	webSocket.close();
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
}
window.onresize = resize();

function connectedToGame() {
	return (typeof webSocket !== "undefined" && webSocket.readyState === webSocket.OPEN);
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
	if (connectedToGame())
		clientInput.angle = coordinateToAngle(e.clientX, e.clientY);
}

/* Returns angle in radians with the following conventions
 *     N: -pi/2		*
 *     E: 	  0		*
 *     S:  pi/2		*
 *     W:    pi     */
function coordinateToAngle(x, y) {
	var x_origin = renderer.width / 2;
	var y_origin = renderer.height / 2;

	return Math.atan2(y - y_origin, x - x_origin);
}

function animationLoop() {
	requestAnimationFrame(animationLoop);
	renderer.render(stage);
}
animationLoop();

window.onload = function() {
	document.getElementById("username").select();
}

document.oncontextmenu = function() {
	return false;
}

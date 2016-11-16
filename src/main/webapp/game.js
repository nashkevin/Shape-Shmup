const INPUT_RATE = 20; // maximum number of inputs per second
var SPEED_CAP = 100;   // maximum speed a player can travel
var webSocket;
var username;
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
var bg = new PIXI.Texture.fromImage("images/background.png");
var bgTile = new PIXI.extras.TilingSprite(bg, 1920, 1080);
bgTile.position.set(0, 0);
bgTile.tilePosition.set(0, 0);
stage.addChild(bgTile);
var player;
var velocityVector = { angle: 0, magnitude: 0 };
var velocityArrow;
var driveVector = { angle: 0, magnitude: 0 };
var driveArrow;
var firingArrow;

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
            //TODO redraw the canvas using the new data
        } catch (error) { // Display non-JSON messages to the chat area
            // if the server is responding to a ping request
            if (e.data === "PONG") {
                addMessageToChat(Date.now() - pingStartTime + " ms");
            } else {
                addMessageToChat(e.data);
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
            drawPlayer();
            drawDriveArrow();
            drawVelocityArrow();
            drawFiringArrow();
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
        if (text === "/clear") {
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
}
window.onresize = resize;

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
        drive();
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
        drive();
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

function drive() {
    // directional client input as boolean array (binary number)
    var movement = [
        !!clientInput.up,
        !!clientInput.down,
        !!clientInput.left,
        !!clientInput.right 
    ];

    var movementCode = 0; // decimal representation of movement
    
    for (var i = 0; i < movement.length; i++) {
        // convert binary to decimal
        movementCode += movement[i] * (1 << (movement.length - 1 - i));
    }
    
    switch (movementCode) {
        case 1:  // right
            driveVector.angle = 0;
            break;
        case 2:  // left
            driveVector.angle = Math.PI;
            break;
        case 4:  // down
            driveVector.angle = Math.PI / 2;
            break;
        case 5:  // down right
            driveVector.angle = Math.PI / 4;
            break;
        case 6:  // down left
            driveVector.angle = 3 * Math.PI / 4;
            break;
        case 8:  // up
            driveVector.angle = -Math.PI / 2;
            break;
        case 9:  // up right
            driveVector.angle = -Math.PI / 4;
            break;
        case 10: // up left
            driveVector.angle = -3 * Math.PI / 4;
            break;
    }

    // if no movement keys are held
    if (movement.indexOf(true) === -1) {
        clearInterval(accelerateID);
        if (!isSpeedDecaying) {
            decaySpeedID = setInterval(decaySpeed, 20);
        }
        isAccelerating = false;
        isSpeedDecaying = true;
        driveVector.magnitude = 0;
    } else {
        clearInterval(decaySpeedID);
        if (!isAccelerating) {
            accelerateID = setInterval(accelerate, 15);
        }
        isSpeedDecaying = false;
        isAccelerating = true;
        driveVector.magnitude = 1;
    }
}

function decaySpeed() {
    if (velocityVector.magnitude > 0) {
        velocityVector.magnitude--;
    } else {
        velocityVector.magnitude = 0;
        clearInterval(decaySpeedID);
        isSpeedDecaying = false;
    }
}

function accelerate() {
    var x = velocityVector.magnitude * Math.cos(velocityVector.angle);
    var y = velocityVector.magnitude * Math.sin(velocityVector.angle);

    x += driveVector.magnitude * Math.cos(driveVector.angle);
    y += driveVector.magnitude * Math.sin(driveVector.angle);
    
    velocityVector.angle = Math.atan2(y, x);

    velocityVector.magnitude = Math.sqrt(x * x + y * y);
    if (velocityVector.magnitude >= SPEED_CAP)
        velocityVector.magnitude = SPEED_CAP;
}

function drawPlayer() {
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

    player = new PIXI.Sprite(renderer.generateTexture(playerShape));
    player.anchor.set(2/3, 0.5);
    player.pivot.set(2/3, 0.5);
    player.position.set(getGameWidth() / 2, getGameHeight() / 2);

    stage.addChild(player);

    var playerName = new PIXI.Text(username, {
        fontFamily: "Arial",
        fontSize: 12 + (16 - username.length),
        align: "center",
        fill: "#F8F8F2",
        dropShadow: true,
        dropShadowColor: "#222222",
        dropShadowDistance: 5,
        stroke: "#131411",
        strokeThickness: 2
    });
    playerName.anchor.set(0.5, 0.5);
    playerName.position = player.position;
    playerName.position.y += 50;
    stage.addChild(playerName);
}

function drawDriveArrow() {
    var arrowShape = new PIXI.Graphics();
    arrowShape.lineStyle(3, 0xFFFFFF, 1);
    arrowShape.moveTo(0, 0);
    arrowShape.lineTo(100, 0);

    driveArrow = new PIXI.Sprite(renderer.generateTexture(arrowShape));
    driveArrow.anchor.set(0, 0);
    driveArrow.pivot.set(0, 0);
    driveArrow.position.set(getGameWidth() / 2, getGameHeight() / 2);

    stage.addChild(driveArrow);
}

function drawVelocityArrow() {
    var arrowShape = new PIXI.Graphics();
    arrowShape.lineStyle(3, 0xFFFF00, 1);
    arrowShape.moveTo(0, 0);
    arrowShape.lineTo(150, 0);

    velocityArrow = new PIXI.Sprite(renderer.generateTexture(arrowShape));
    velocityArrow.anchor.set(0, 0);
    velocityArrow.pivot.set(0, 0);
    velocityArrow.position.set(getGameWidth() / 2, getGameHeight() / 2);

    stage.addChild(velocityArrow);
    velocityArrow.scale.set(0, 0); // initially represent no speed
}

function drawFiringArrow() {
    var arrowShape = new PIXI.Graphics();
    arrowShape.lineStyle(3, 0xFF0000, 1);
    arrowShape.moveTo(0, 0);
    arrowShape.lineTo(100, 0);

    firingArrow = new PIXI.Sprite(renderer.generateTexture(arrowShape));
    firingArrow.anchor.set(0, 0);
    firingArrow.pivot.set(0, 0);
    firingArrow.position.set(getGameWidth() / 2, getGameHeight() / 2);

    stage.addChild(firingArrow);
    firingArrow.visible = false; // initially not firing
}

function animationLoop() {
    requestAnimationFrame(animationLoop);
    if (player != null) {
        
        if (clientInput.angle != null)
            player.rotation = clientInput.angle + Math.PI;

        if (driveVector.magnitude != 0) {
            driveArrow.rotation = driveVector.angle;
            driveArrow.visible = true;
        }
        else {
            driveArrow.visible = false;
        }

        if (clientInput.isFiring != null) {
            firingArrow.rotation = player.rotation - Math.PI;
            firingArrow.visible = true;
        } else {
            firingArrow.visible = false;
        }
        
        velocityArrow.rotation = velocityVector.angle;
        velocityArrow.scale.set(velocityVector.magnitude / SPEED_CAP, 1);

        var x = velocityVector.magnitude * Math.cos(velocityVector.angle);
        var y = velocityVector.magnitude * Math.sin(velocityVector.angle);
        bgTile.tilePosition.x -= x / 10;
        bgTile.tilePosition.y -= y / 19;
    }
    renderer.render(stage);
}
animationLoop();

window.onload = function() {
    document.getElementById("username").select();
}

document.oncontextmenu = function() {
    return false;
}

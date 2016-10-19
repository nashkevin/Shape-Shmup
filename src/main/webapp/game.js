var webSocket;
var messages = document.getElementById("messages");


function openSocket() {
	// Ensures only one connection is open at a time
	if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
		writeResponse("WebSocket is already opened.");
		return;
	}
	// Create a new instance of the websocket
	var url = "ws://" + window.location.host + "/socket";
	webSocket = new WebSocket(url);
	 
	// Binds functions to the listeners for the websocket.
	webSocket.onopen = function(event) {
		if(event.data === undefined) 
			return;

		writeResponse(event.data);
	};

	webSocket.onmessage = function(event) {
		writeResponse(event.data);
	};

	webSocket.onclose = function(event) {
		writeResponse("Connection closed");
	};
}

// Sends the value of the text input to the server
function send() {
	var text = document.getElementById("messageinput").value;
	webSocket.send(text);
}

function closeSocket() {
	webSocket.close();
}

function writeResponse(text) {
	messages.innerHTML += "<br/>" + text;
}

// Key listener
window.onkeydown = function (e) {
	if (document.getElementById("messageinput") !== document.activeElement) {
		var code = e.keyCode ? e.keyCode : e.which;
		switch (code) {
			case 87: case 38:  // 'w' or up
				webSocket.send("Up");
				break;
			case 65: case 37: // 'a' or left 
				webSocket.send("Left");
				break;
			case 83: case 40: // 's' or down
				webSocket.send("Down");
				break;
			case 68: case 39: // 'd' or right
				webSocket.send("Right");
				break;
		}
	}
};
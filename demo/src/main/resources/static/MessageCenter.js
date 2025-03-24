const messageInput = document.getElementById('message-input');
const chatMessages = document.getElementById('chat-messages');
const sendButton = document.getElementById('send-button');
const clearButton = document.getElementById('clear-button');
document.getElementById("clear-button").addEventListener("click", function() {
  document.getElementById("chat-messages").innerHTML = "";
});

var latestMessage = -1;

var userMessages = [];

// Function to send message
function sendMessage() {
    const message = messageInput.value;

    var xhr = new XMLHttpRequest();

    xhr.onload = function () {
        // Clear message input field
        messageInput.value = '';

        userMessages.push(xhr.responseText);
    };

    xhr.open("POST", "/MessageCenter.html", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    xhr.send("message=" + message);

}

// Add event listener to send button
sendButton.addEventListener('click', sendMessage);

// Add event listener for Enter key press
messageInput.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
});

// Function to receive message from server
function receiveMessage(message, sender, messageID, validUser) {
console.log(validUser);
    // Create message element and add to chat display
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');
    var s = (userMessages.includes(messageID, 0) ? "You" : sender);
    var imageSRC = (validUser  === 'true' ?  "/getPfp?username=" + sender : "/photos/defaultPFP.png");
    messageElement.innerHTML = '<img class="message-icon" src="' + imageSRC +'"><span class="message-sender">' +s + '</span><span class="message-content">'+ message + '</span>';
    chatMessages.appendChild(messageElement);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function Get(yourUrl){
   var Httpreq = new XMLHttpRequest(); // a new request
   Httpreq.open("GET",yourUrl,false);
   Httpreq.send(null);
   return Httpreq.responseText;
}

var intervalId = window.setInterval(function(){
    JSON.parse(Get("/getMessages")).forEach(function(str){
        var rest = str.substring(str.indexOf('|') + 1);
        var next = rest.substring(rest.indexOf('|') + 1)
        var messageID = next.substring(0, next.indexOf('|'));
        if(messageID > latestMessage){
            receiveMessage(next.substring(next.indexOf('|') + 1), str.substring(0, str.indexOf('|')), messageID, rest.substring(0, rest.indexOf('|')));
            latestMessage = messageID
        }
    });

}, 200);
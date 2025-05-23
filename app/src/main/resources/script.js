document.addEventListener("DOMContentLoaded", () => {
    const username = prompt("Enter your username:");
    const socket = new WebSocket(`ws://localhost:8080/yapyapyap/chat/${username}`);

    const chatContainer = document.querySelector(".chat");
    const inputBox = document.querySelector(".text-box");
    const sendButton = document.querySelector(".send-button");

    socket.onopen = () => {
        console.log("Connected to WebSocket as", username);
        const initPayload = {
            sender: username,
            msg: `${username}`
        };
        socket.send(JSON.stringify(initPayload));
    };

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        appendMessage(data.sender, data.msg);
    };

    socket.onclose = () => {
        console.log("Disconnected from WebSocket");
    };

    socket.onerror = (err) => {
        console.error("WebSocket error:", err);
    };

    sendButton.addEventListener("click", () => {
        const message = inputBox.value.trim();
        if (message !== "") {
            const payload = {
                sender: username,
                msg: message
            };
            socket.send(JSON.stringify(payload));
            appendMessage("You", message, true);
            inputBox.value = "";
        }
        console.log(message);
    });

    function appendMessage(sender, message, isSender = false) {
        const msgDiv = document.createElement("div");
        msgDiv.classList.add(isSender ? "sender" : "recipient");

        msgDiv.innerHTML = `
            <div class="name">${sender}</div>
            <hr class="name-line">
            <div class="message">${message}</div>
        `;

        chatContainer.appendChild(msgDiv);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }
});

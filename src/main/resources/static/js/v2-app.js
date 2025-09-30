async function handleSubmit() {
    try {
        const statement = document.getElementById("question").value;
        if (statement === "") return;

        const accessToken = document.getElementById("accessToken").value.trim();
        if (accessToken === "") {
            alert("Access Token을 입력하세요");
            return;
        }

        const chatPanel = document.getElementById("chatPanel");

        const userMessageDiv = document.createElement('div');
        userMessageDiv.className = 'chat-message user-message';
        userMessageDiv.innerHTML = `<strong>나:</strong> ${statement.replace(/</g, '&lt;').replace(/>/g, '&gt;')}`;
        chatPanel.appendChild(userMessageDiv);
        chatPanel.scrollTop = chatPanel.scrollHeight;

        document.getElementById("spinner").classList.add('active');

        const body = {
            question: statement
        };

        const response = await fetch('/api/ai/advisor/ask', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + accessToken
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        const markdownHtml = DOMPurify.sanitize(marked.parse(result.answerMarkdown));

        const aiMessageDiv = document.createElement('div');
        aiMessageDiv.className = 'chat-message ai-message';
        aiMessageDiv.innerHTML = `<strong>Agent:</strong><div class="response-text">${markdownHtml}</div>`;
        chatPanel.appendChild(aiMessageDiv);
        chatPanel.scrollTop = chatPanel.scrollHeight;

    } catch (error) {
        console.error('Error:', error);
        alert('오류 발생: ' + error.message);
    } finally {
        document.getElementById("spinner").classList.remove('active');
        document.getElementById('question').value = '';
    }
}

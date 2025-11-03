async function handleSubmit() {
    try {
        const accessToken = document.getElementById("accessToken").value.trim();
        if (accessToken === "") {
            alert("Access Token을 입력하세요 (Prefix 제외)");
            return;
        }

        const chatPanel = document.getElementById("chatPanel");

        const userMessageDiv = document.createElement('div');
        userMessageDiv.className = 'chat-message user-message';
        userMessageDiv.innerHTML = `<strong>me:</strong> 포트폴리오를 바탕으로, 자산 현황 및 투자 방향 제시`;
        chatPanel.appendChild(userMessageDiv);
        chatPanel.scrollTop = chatPanel.scrollHeight;

        document.getElementById("spinner").classList.add('active');

        const response = await fetch('/ai-summary/portfolio', {
            method: "GET",
            headers: {
                'Accept': 'application/json',
                'Authorization': "Bearer " + accessToken
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        const markdownSource =
            (result && (result.data || result.answerMarkdown || result.text || result.result)) || "";

        const markdownHtml = DOMPurify.sanitize(marked.parse(markdownSource));

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
        const q = document.getElementById('question');
        if (q) q.value = '';
    }
}

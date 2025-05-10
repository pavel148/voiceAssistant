const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
if (SpeechRecognition) {
    const recognition = new SpeechRecognition();
    recognition.lang = 'ru-RU';
    recognition.interimResults = false;
    document.getElementById('start-record').addEventListener('click', () => recognition.start());
    recognition.addEventListener('result', event => {
        const transcript = event.results[0][0].transcript;
        document.getElementById('userInput').value = transcript;
    });
}

// Speech Synthesis
document.getElementById('audio-play').addEventListener('click', () => {
    const text = document.getElementById('botReply').innerText;
    if (text) {
        const utterance = new SpeechSynthesisUtterance(text);
        utterance.lang = 'ru-RU';
        speechSynthesis.speak(utterance);
    }
});

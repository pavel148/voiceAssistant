document.addEventListener('DOMContentLoaded', () => {
    const wakeWord    = 'привет ассистент';
    const userInputEl = document.getElementById('userInput');
    const botReplyEl  = document.getElementById('botReply');
    const recordBtn   = document.getElementById('start-record');
    const playBtn     = document.getElementById('audio-play');
    const stopSpeech  = document.getElementById('stop-speech');

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        console.warn('[Speech API] not supported');
        return;
    }

    const recognizer = new SpeechRecognition();
    recognizer.lang           = 'ru-RU';
    recognizer.interimResults = true;    // получаем промежуточные результаты
    recognizer.continuous     = true;    // чтобы получать несколько onresult подряд

    // При любом результате — пишем в textarea (live-update)
    recognizer.onresult = event => {
        // Собираем все результаты (interim + final)
        let transcript = '';
        for (let i = 0; i < event.results.length; i++) {
            transcript += event.results[i][0].transcript;
        }
        userInputEl.value = transcript.trim();
        console.log('[Recognizer] interim/final:', transcript);
    };

    recognizer.onerror = evt => {
        console.error('[Recognizer] error:', evt.error);
    };

    recognizer.onend = () => {
        console.log('[Recognizer] stopped listening');
        // Признак, что дальше мы вызовем sendChatRequest вручную
    };

    // Старт распознавания при зажатии кнопки
    recordBtn.addEventListener('mousedown', () => {
        console.log('[Recognizer] start listening');
        try {
            recognizer.start();
        } catch (e) {
            console.warn('Cannot start recognition:', e);
        }
    });

    // Останавливаем при отпускании или уходе курсора с кнопки
    const stopRecognition = () => {
        if (recognizer) {
            console.log('[Recognizer] stop listening');
            recognizer.stop();
            // После остановки берём текущее содержимое и шлём запрос
            const fullText = userInputEl.value.trim().toLowerCase();
            if (fullText.startsWith(wakeWord)) {
                const query = fullText.replace(wakeWord, '').trim();
                if (query) {
                    sendChatRequest(query);
                }
            }
        }
    };
    recordBtn.addEventListener('mouseup',   stopRecognition);
    recordBtn.addEventListener('mouseleave', stopRecognition);

    // Функция отправки запроса и озвучки ответа
    function sendChatRequest(text) {
        console.log('[API] request:', text);
        fetch('/api/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify([
                { role: 'system', content: 'You are a helpful assistant.' },
                { role: 'user',   content: text }
            ])
        })
            .then(res => res.json())
            .then(data => {
                const reply = data.choices?.[0]?.message?.content || 'Ошибка ответа';
                botReplyEl.innerText = reply;
                speakText(reply);
            })
            .catch(err => {
                console.error('[API] error:', err);
                botReplyEl.innerText = 'Ошибка получения ответа';
            });
    }

    function speakText(text) {
        // Останавливаем распознавание, чтобы tts не мешало
        recognizer.stop();
        const utt = new SpeechSynthesisUtterance(text);
        utt.lang = 'ru-RU';
        utt.onend = () => {
            console.log('[TTS] done');
        };
        speechSynthesis.speak(utt);
    }

    playBtn.addEventListener('click', () => {
        const text = botReplyEl.innerText.trim();
        if (text) speakText(text);
    });

    // Кнопка «🔇 Замолчать» — отменяет всю текущую и запланированную речь
    stopSpeech.addEventListener('click', () => {
        console.log('[TTS] cancel');
        speechSynthesis.cancel();
    });
});

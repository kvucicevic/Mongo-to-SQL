package message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {

    private final String text;
    private final LocalDateTime localDateTime;

    public Message(String text) {
        this.text = text;
        this.localDateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "[" + localDateTime.format(dateTimeFormatter) + "] " + text;
    }
}

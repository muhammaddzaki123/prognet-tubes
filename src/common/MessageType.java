package common;

public enum MessageType {
    // Client → Server
    CREATE_ROOM,
    JOIN_ROOM,
    START_GAME,
    FLIP_CARD,
    CHAT_MESSAGE,
    DISCONNECT,

    // Server → Client
    ROOM_CREATED,
    ROOM_JOINED,
    PLAYER_JOINED,
    GAME_STARTED,
    CARD_FLIPPED,
    MATCH_FOUND,
    NO_MATCH,
    TURN_CHANGED,
    SCORE_UPDATE,
    GAME_OVER,
    PLAYER_LEFT,
    ERROR
}

package prognet.common;

public enum MessageType {
    // Client → Server
    CREATE_ROOM,
    JOIN_ROOM,
    START_GAME,
    FLIP_CARD,
    CHAT_MESSAGE,
    DISCONNECT,
    REMATCH_VOTE, // Player votes for rematch
    LEAVE_TO_HOME, // Player leaves to home

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
    ERROR,
    REMATCH_VOTE_UPDATE, // Update on rematch votes
    REMATCH_ACCEPTED, // Both players agreed to rematch
    REMATCH_DECLINED // One player declined
}

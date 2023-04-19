package ca.dal.cs.onlinebartertrader.MessagePages;

/**
 * determines the reference of a chat between two uesers
 */
public class ChatLogic {
    private ChatLogic() {
    }

    /**
     * Returns the name (reference) of a chat between two users, as stored on the database.
     * The name is a combination of the two usernames, ordered alphabetically and separated by an
     * underscore.
     *
     * @param username: Username of the current user
     * @param chatWith: Username of the user that the current user is chatting with
     * @return The name of the chat as stored on the database
     */
    protected static String getChatRef(String username, String chatWith) {
        String chatName;
        if (username.compareTo(chatWith) < 0) {
            chatName = username + "_" + chatWith;
        } else {
            chatName = chatWith + "_" + username;
        }
        return chatName;
    }
}

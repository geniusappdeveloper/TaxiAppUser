package app.user.strip;

import com.stripe.android.model.Token;

public interface TokenList {
    public void addToList(Token token);
}

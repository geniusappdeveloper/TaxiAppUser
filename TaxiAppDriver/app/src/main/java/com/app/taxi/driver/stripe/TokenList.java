package com.app.taxi.driver.stripe;

import com.stripe.android.model.Token;

public interface TokenList {
    public void addToList(Token token);
}

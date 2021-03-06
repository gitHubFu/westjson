package com.github.bingoohuang.westjson.impl;

import com.github.bingoohuang.westjson.utils.StrBuilder;

import static com.github.bingoohuang.westjson.utils.WestJsonUtils.*;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2017/2/1.
 */
public class WestJsonQuoter {
    private String json;
    private StrBuilder res;
    private int ii;

    int i = 0;
    int quoted = -1;
    char p, ch, n;

    public void init(String json) {
        this.json = json;
        this.ii = json.length();
        this.res = new StrBuilder((int) (json.length() * 1.5));
    }

    public String quote(String json) {
        init(json);

        for (; i < ii; ++i) {
            p = i == 0 ? ' ' : json.charAt(i - 1);
            ch = json.charAt(i);
            n = i + 1 < ii ? json.charAt(i + 1) : ' ';

            if (processEscape()) continue;

            if (quoted < 0) {
                if (processLeftBrace()) continue;
                if (processColon()) continue;
                if (processComma()) continue;
                if (processRightBrace()) continue;
            } else {
                if (processQuote() && i > quoted + 1) {
                    quoted = -1;
                    continue;
                }
            }

            res.p(ch);
        }

        return res.toString();
    }

    private boolean processQuote() {
        return ch == '\"';
    }

    private boolean processEscape() {
        if (ch != '\\') return false;

        ++i;
        if (!isMeta(n)) res.p(ch);
        res.p(n);
        return true;
    }

    private boolean processLeftBrace() {
        if (!isLBoundary(ch)) return false;

        res.p(ch);
        quoted = n == '"' ? i : -1;
        if (quoted < 0 && !isBoundary(n)) res.p('"');
        return true;
    }

    private boolean processColon() {
        if (ch != ':') return false;

        if (!isBoundary(p)) res.p('"');
        res.p(ch);
        quoted = n == '"' ? i : -1;
        if (quoted < 0 && !isLBoundary(n) && !isRKey(json, i, ii)) res.p('"');

        return true;
    }

    private boolean processComma() {
        if (ch != ',') return false;

        if (!isRBoundary(p) && !isLKey(json, i, ii)) res.p('"');
        res.p(ch);
        quoted = n == '"' ? i : -1;
        if (quoted < 0 && !isLBoundary(n) && !isRKey(json, i, ii)) res.p('"');

        return true;
    }


    private boolean processRightBrace() {
        if (!isRBoundary(ch)) return false;

        if (quoted < 0 && !isBoundary(p) && !isLKey(json, i, ii)) res.p('"');
        res.p(ch);
        quoted = -1;
        return true;
    }

}

package dev.kosmx.playerAnim.core.util;

import lombok.Getter;

/**
 * Easings form <a href="https://easings.net/">easings.net</a><br>
 * + constant + linear
 */
public enum Ease {
    LINEAR(0, f -> f), CONSTANT(1, f -> 0f),
    INSINE(6, Easing::inSine), OUTSINE(7, Easing::outSine), INOUTSINE(8, Easing::inOutSine),
    INCUBIC(9, Easing::inCubic), OUTCUBIC(10, Easing::outCubic), INOUTCUBIC(11, Easing::inOutCubic),
    INQUAD(12, Easing::inQuad), OUTQUAD(13, Easing::outQuad), INOUTQUAD(14, Easing::inOutQuad),
    INQUART(15, Easing::inQuart), OUTQUART(16, Easing::outQuart), INOUTQUART(17, Easing::inOutQuart),
    INQUINT(18, Easing::inQuint), OUTQUINT(19, Easing::outQuint), INOUTQUINT(20, Easing::inOutQuint),
    INEXPO(21, Easing::inExpo), OUTEXPO(22, Easing::outExpo), INOUTEXPO(23, Easing::inOutExpo),
    INCIRC(24, Easing::inCirc), OUTCIRC(25, Easing::outCirc), INOUTCIRC(26, Easing::inOutCirc),
    INBACK(27, Easing::inBack), OUTBACK(28, Easing::outBack), INOUTBACK(29, Easing::inOutBack),
    INELASTIC(30, Easing::inElastic), OUTELASTIC(31, Easing::outElastic), INOUTELASTIC(32, Easing::inOutElastic),
    INBOUNCE(33, Easing::inBounce), OUTBOUNCE(34, Easing::outBack), INOUTBOUNCE(35, Easing::inOutBounce);

    @Getter
    final byte id;
    private final _F impl;

    /**
     * @param id   id
     * @param impl implementation
     */
    Ease(byte id, _F impl){
        this.id = id;
        this.impl = impl;
    }

    /**
     * @param id   id
     * @param impl implementation
     */
    Ease(int id, _F impl) {
        this((byte) id, impl);
    }

    /**
     * Run the easing
     * @param f float between 0 and 1
     * @return ease(f)
     */
    public float invoke(float f) {
        return impl.invoke(f);
    }

    /**
     * Run the easing
     * @param t float between 0 and 1
     * @param n float easing argument
     * @return ease(t, n)
     */
    public float invoke(float t, Float n) {
        if (n != null) {
            switch (id) {
                case 27:
                    return Easing.back(t, n);
                case 28:
                    return Easing.easeOut(t, n, Easing::back);
                case 29:
                    return Easing.easeInOut(t, n, Easing::back);
                case 30:
                    return Easing.elastic(t, n);
                case 31:
                    return Easing.easeOut(t, n, Easing::elastic);
                case 32:
                    return Easing.easeInOut(t, n, Easing::elastic);
                case 33:
                    return Easing.bounce(t, n);
                case 34:
                    return Easing.easeOut(t, n, Easing::bounce);
                case 35:
                    return Easing.easeInOut(t, n, Easing::bounce);
            }
        }
        return impl.invoke(t);
    }

    //To be able to send these as bytes instead of String names.
    public static Ease getEase(byte b){
        for(Ease ease:Ease.values()){
            if(ease.id == b) return ease;
        }
        return LINEAR;
    }

    private interface _F {
        float invoke(float f);
    }
}

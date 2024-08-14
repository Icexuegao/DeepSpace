function yuanEffect(time, radius, N, angle, counterclockwise) {
    return new Effect(time, cons(e => {
        var num = 360 / N,
            A = 0;
        for (let i = 0; i < num; i++) {
            Draw.color(new Color(0.5, i / num, i / num, 0.5));
            for (let j = 0; j < N; j++) {
                if (counterclockwise) {
                    A = i + num * j + Time.time + angle
                } else {
                    A = -(i + num * j + Time.time + angle)
                }
                Fill.circle(e.x + Angles.trnsx(A, radius), e.y + Angles.trnsy(A, radius), 2 * i / num)
            }
        }
    }))
};
exports.yuanEffect = yuanEffect;


function shiziEffect(time, siza, length, speed) {
    return new Effect(time, cons(e => {
        let size = siza / 3 * 16;
        Draw.color(new Color(0, 1, 1, 0.5));
        Lines.rect(e.x - size / 2, e.y - size / 2, size, size);
        let x = e.x + 0.5 * size * Mathf.cosDeg(speed * Time.time) * Math.abs(Mathf.cosDeg(speed * Time.time)),
            y = e.y + 0.5 * size * Mathf.sinDeg(speed * Time.time) * Math.abs(Mathf.sinDeg(speed * Time.time));
        for (let i = 0; i < 4; i++) {
            Lines.lineAngle(x, y, 90 * i, length)
        }
    }))
};
exports.shiziEffect = shiziEffect;


function yuanshizi(time, radius, angle, judge) {
    return new Effect(time, cons(e => {
        let A0 = 0,
            A1 = 0;
        for (let i = 0; i < angle; i++) {
            let i_ = angle - i;
            Draw.color(new Color(i_ / angle, 2 * i_ / (angle + i_), 3 * i_ / (angle + 2 * i_), 1));
            for (let j = 0; j < 4; j++) {
                if (judge) {
                    A0 = -Time.time * i / angle + 90 * j;
                    A1 = -Time.time * (1 - i / angle) + i + 90 * (j + 2)
                } else {
                    A0 = -Time.time * i / angle + 90 * j;
                    A1 = -Time.time * (1 - i_ / angle) + i + 90 * (j + 2)
                }
                let x = e.x + Angles.trnsx(A0, radius) + Angles.trnsx(A1, radius);
                let y = e.y + Angles.trnsy(A0, radius) + Angles.trnsy(A1, radius);
                Fill.circle(x, y, 2 * i_ / angle)
            }
        }
    }))
};
exports.yuanshizi = yuanshizi;


function zhunxin(time, siza, angle, speed, counterclockwise, onLine, vertical, color) {
    return new Effect(time, cons(e => {
        let size = siza / 3 * 16,
            length = 0.5 * size,
            A = 0,
            cos = 0,
            sin = 0,
            x = 0,
            y = 0,
            k = 0;
        for (let i = 0; i < length; i++) {
            let i_ = length - i;
            Draw.color(new Color(i_ / length, 2 * (length + i_) / i_, 3 * (length + 2 * i_) / i_, i / length));
            for (let j = 0; j < 4; j++) {
                if (counterclockwise) {
                    A = speed * Time.time + angle + 90 * j;
                    k = i;
                } else {
                    A = -(speed * Time.time + angle + 90 * j);
                    k = -i;
                }
                if (vertical) {
                    x = e.x + Mathf.cosDeg(90 * j) * Mathf.cosDeg(Time.time * i_ / length) * (length + i);
                    y = e.y + Mathf.sinDeg(90 * j) * Mathf.cosDeg(Time.time * i_ / length) * (length + i_);
                } else if (onLine) {
                    cos = Mathf.cosDeg(A + k);
                    sin = Mathf.sinDeg(A + k);
                    x = e.x + length * cos * Math.abs(cos);
                    y = e.y + length * sin * Math.abs(sin);
                } else {
                    cos = Mathf.cosDeg(A);
                    sin = Mathf.sinDeg(A);
                    x = e.x + cos * Math.abs(cos) * k;
                    y = e.y + sin * Math.abs(sin) * k;
                }
                Fill.circle(x, y, 2 * k / length)
            }
        }
    }))
};
exports.zhunxin = zhunxin;

exports.miaozhun = (time, siza, angle, speed, color) => {
    var size = siza / 3 * 16,
        length = 0.5 * size,
        A = 0,
        cos = 0,
        sin = 0,
        x = 0,
        y = 0,
        k = 0;
    for (let i = 0; i < length; i++) {
        let i_ = length - i;
        Draw.color(Color.gray);
        for (let j = 0; j < 4; j++) {
            A = speed * Time.time + angle + 90 * j;
            k = i;
            cos = Mathf.cosDeg(A);
            sin = Mathf.sinDeg(A);
            x = this.x + cos * Math.abs(cos) * k;
            y = this.y + sin * Math.abs(sin) * k;
            Fill.circle(x, y, 2 * k / length)
        }
    }
};
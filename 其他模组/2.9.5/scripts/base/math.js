exports.drag = (speed, lifetime, drag) => {
    let len = 0, vel = speed;
    for (let i = 0; i < lifetime; i++) {
        vel *= Math.max(1 - drag, 0);
        len += vel;
    }
    ;
    return (len / 8).toFixed(3);
}

exports.missile = (speed, lifetime, miss) => {
    let len = 0;
    for (let i = 0; i < lifetime; i++) {
        len += Math.pow(Math.min(i / miss, 1), 2) * speed;
    }
    ;
    return (len / 8).toFixed(3);
}
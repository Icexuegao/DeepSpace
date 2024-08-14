let lib = require("base/snakelib");

const exampleSnakeEnd = lib.segment("snakeEnd", {
    hitSize: 10,
    offsetSegment: -2,
    health: 600
}, {});

exampleSnakeEnd.defaultController = lib.segemntAI;

const exampleSnakeBody = lib.segment("snakeBody", {
    hitSize: 10,
    engines: 2,
    engineSize: 3,
    engineRotOffset: 90,
    engineOffset: 10,
    offsetSegment: -1,
    health: 600
}, {});

exampleSnakeBody.defaultController = lib.segemntAI;

const exampleSnake = lib.head("snake", {
    body: exampleSnakeBody,
    end: exampleSnakeEnd,
    lengthSnake: 10,
    hitSize: 10,
    speed: 1.5,
    health: 1000
}, {});


const weaponBombBullet = extend(MissileBulletType, 2.7, 12, {
    width: 6,
    height: 8,
    shrinkY: 0,
    drag: -0.003,
    homingRange: 60,
    keepVelocity: false,
    splashDamageRadius: 10,
    splashDamage: 5,
    lifetime: 80,
    trailColor: Color.gray,
    backColor: Pal.bulletYellowBack,
    frontColor: Pal.bulletYellow,
    hitEffect: Fx.blastExplosion,
    despawnEffect: Fx.blastExplosion,
    weaveScale: 8,
    weaveMag: 1
})

const weaponBomb = extend(Weapon, "snakeBody-weapon", {
    reload: 80,
    shots: 1,
    inaccuracy: 4,
    ejectEffect: Fx.casing3,
    shootSound: Sounds.artillery,
    rotate: true,
    shotDelay: 1,
    x: 0,
    y: -8 / 4,
    shootY: 17 / 4,
    mirror: false,
    top: true,
    shootCone: 2,
    bullet: weaponBombBullet
})

exampleSnakeBody.weapons.add(weaponBomb);
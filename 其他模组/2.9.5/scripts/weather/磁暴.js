const 磁暴 = extend(ParticleWeather, "磁暴", {
    update(state) {
        //用于获取世界地图大小
        let w = Vars.world.tiles.width,
            h = Vars.world.tiles.height,
            //设置闪电生成的范围
            x = Mathf.random(0, w * 8),
            y = Mathf.random(0, h * 8),
            //设置闪电长度
            length = Mathf.random(25, 100),
            //设置随机角度
            angle = Mathf.random(0, 360);
        //创建
        Lightning.create(Team.derelict, Color.white, 100, x, y, angle, length);
    }
});
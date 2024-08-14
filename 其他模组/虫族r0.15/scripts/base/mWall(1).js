const mWall = new Wall("m-wall");
mWall.buildVisibility = BuildVisibility.shown;
mWall.update = true;
mWall.buildType = prov(() => extend(Wall.WallBuild, mWall, {//设置build,改名改第2个
    updateTile() {
        this.super$updateTile();//复用原版方法

        let other = Puddles.get(this.tile);//获取地板上的水坑
        if (other != null && other.liquid == Liquids.neoplasm && other.amount > 0.001) {//判断水坑是否有效
            this.damage(5 / 60)//对建筑造成伤害

            other.amount -= 5 / 60//减少水坑液体量
        }
    }
}))
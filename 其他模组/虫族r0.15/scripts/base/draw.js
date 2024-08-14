function DrawShakeRegion(suffix, range, cycle) {
    return extend(DrawRegion, suffix, {
        draw(build) {

            let z = Draw.z();
            Draw.rect(
                this.region,
                build.x,
                build.y,
                Math.sin(build.totalProgress * Math.PI / (cycle / 2)) * range
            );
            Draw.z(z);
        }
    })
}

exports.DrawShakeRegion = DrawShakeRegion
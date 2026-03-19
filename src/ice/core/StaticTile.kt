package ice.core;

import arc.math.geom.Point2

class StaticTile {
    companion object {
        var tileMap = charArrayOf(
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 39.toChar(), 38.toChar(), 39.toChar(), 41.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 47.toChar(), 42.toChar(), 47.toChar(), 45.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 39.toChar(), 38.toChar(), 39.toChar(), 41.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 47.toChar(), 42.toChar(), 47.toChar(), 45.toChar(),
            12.toChar(), 13.toChar(), 12.toChar(), 13.toChar(), 24.toChar(), 25.toChar(), 24.toChar(), 28.toChar(),
            12.toChar(), 13.toChar(), 12.toChar(), 13.toChar(), 24.toChar(), 25.toChar(), 24.toChar(), 28.toChar(),
            15.toChar(), 14.toChar(), 15.toChar(), 14.toChar(), 27.toChar(), 26.toChar(), 27.toChar(),  7.toChar(),
            15.toChar(), 14.toChar(), 15.toChar(), 14.toChar(), 31.toChar(),  4.toChar(), 31.toChar(), 46.toChar(),
            12.toChar(), 13.toChar(), 12.toChar(), 13.toChar(), 24.toChar(), 25.toChar(), 24.toChar(), 28.toChar(),
            12.toChar(), 13.toChar(), 12.toChar(), 13.toChar(), 24.toChar(), 25.toChar(), 24.toChar(), 28.toChar(),
            11.toChar(),  6.toChar(), 11.toChar(),  6.toChar(), 19.toChar(), 40.toChar(), 19.toChar(), 21.toChar(),
            11.toChar(),  6.toChar(), 11.toChar(),  6.toChar(), 35.toChar(), 23.toChar(), 35.toChar(), 30.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 39.toChar(), 38.toChar(), 39.toChar(), 41.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 47.toChar(), 42.toChar(), 47.toChar(), 45.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             0.toChar(),  1.toChar(),  0.toChar(),  1.toChar(), 36.toChar(), 37.toChar(), 36.toChar(), 44.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 39.toChar(), 38.toChar(), 39.toChar(), 41.toChar(),
             3.toChar(),  2.toChar(),  3.toChar(),  2.toChar(), 47.toChar(), 42.toChar(), 47.toChar(), 45.toChar(),
            12.toChar(),  8.toChar(), 12.toChar(),  8.toChar(), 24.toChar(), 16.toChar(), 24.toChar(), 20.toChar(),
            12.toChar(),  8.toChar(), 12.toChar(),  8.toChar(), 24.toChar(), 16.toChar(), 24.toChar(), 20.toChar(),
            15.toChar(),  5.toChar(), 15.toChar(),  5.toChar(), 27.toChar(), 43.toChar(), 27.toChar(), 32.toChar(),
            15.toChar(),  5.toChar(), 15.toChar(),  5.toChar(), 31.toChar(), 22.toChar(), 31.toChar(), 29.toChar(),
            12.toChar(),  8.toChar(), 12.toChar(),  8.toChar(), 24.toChar(), 16.toChar(), 24.toChar(), 20.toChar(),
            12.toChar(),  8.toChar(), 12.toChar(),  8.toChar(), 24.toChar(), 16.toChar(), 24.toChar(), 20.toChar(),
            11.toChar(), 10.toChar(), 11.toChar(), 10.toChar(), 19.toChar(),  9.toChar(), 19.toChar(), 17.toChar(),
            11.toChar(), 10.toChar(), 11.toChar(), 10.toChar(), 35.toChar(), 18.toChar(), 35.toChar(), 33.toChar()
        )
        var proximityPoint: Array<Point2> = arrayOf(
            Point2(1, 0),
            Point2(1, 1),
            Point2(0, 1),
            Point2(-1, 1),
            Point2(-1, 0),
            Point2(-1, -1),
            Point2(0, -1),
            Point2(1, -1),
        )
    }
}

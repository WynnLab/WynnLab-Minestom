package com.wynnlab.minestom.util

import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec

fun Pos.sub2Vec(x: Double, y: Double, z: Double) = Vec(this.x() - x, this.y() - y, this.z() - z)

operator fun Pos.plus(p: Point) = add(p)
operator fun Vec.plus(p: Point) = add(p)

operator fun Pos.minus(p: Point) = sub(p)
operator fun Vec.minus(p: Point) = sub(p)

operator fun Vec.times(s: Double) = mul(s)

operator fun Vec.rangeTo(v: Vec) = VectorsBuilder(this, v)
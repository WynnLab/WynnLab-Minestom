package com.wynnlab.minestom.reqistry

import com.wynnlab.minestom.base.BaseClass

object ClassRegistry : Registry<BaseClass>() {
    override val entries = mutableListOf<BaseClass>()
}
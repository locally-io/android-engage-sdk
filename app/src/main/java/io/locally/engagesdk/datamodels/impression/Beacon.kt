package io.locally.engagesdk.datamodels.impression

import io.locally.engagesdk.datamodels.impression.ImpressionType
import io.locally.engagesdk.datamodels.impression.Proximity

class Beacon(val type: ImpressionType, val proximity: Proximity, val major: Int, val minorDec: Int)
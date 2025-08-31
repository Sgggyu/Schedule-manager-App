package com.example.schedulemanager.logic.model

import com.example.schedulemanager.R



class BagOfRender(val info: String,val icon:Int,val color: Int)

private val render = mapOf(
    TYPE.SLEEP to BagOfRender("睡觉",R.drawable.ic_sleep,R.color.sleep),
    TYPE.WORK to BagOfRender("工作",R.drawable.ic_work,R.color.work),
    TYPE.EXERCISE to BagOfRender("锻炼",R.drawable.ic_exercise,R.color.exercise),
    TYPE.STUDY to BagOfRender("学习",R.drawable.ic_study,R.color.study),
    TYPE.MEAL to BagOfRender("吃饭",R.drawable.ic_meal,R.color.meal),
    TYPE.LEISURE to BagOfRender("娱乐",R.drawable.ic_leisure,R.color.leisure),
    TYPE.RELAX to BagOfRender("休息",R.drawable.ic_relax,R.color.relax),
    TYPE.OTHER to BagOfRender("其他",R.drawable.ic_other,R.color.other),

)

fun getRender(type: Int): BagOfRender{
    return render[type] ?: render[TYPE.OTHER]!!
}
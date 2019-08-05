package com.hacksterkrishna.a1teachers.models

/**
 * Created by krishna on 27/4/18.
 */
class Classlist{

    private var class_name:String?=null


    fun setClassname(class_name:String){
        this.class_name=class_name
    }

    fun getClassname(): String {
        return class_name!!
    }
}
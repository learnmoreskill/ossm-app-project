package com.hacksterkrishna.a1teachers.models

/**
 * Created by krishna on 27/4/18.
 */
class Sectionlist{

    private var section_name:String?=null

    fun setSectionname(section_name:String){
        this.section_name=section_name
    }

    fun getSectionname(): String {
        return section_name!!
    }
}
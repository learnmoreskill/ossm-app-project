package com.hacksterkrishna.a1teachers.models

/**
 * Created by krishna on 31/12/17.
 */
class Teacher{

    private var id:Int?=null
    private var name:String?=null
    private var email:String?=null
    private var address:String?=null
    private var mobile:String?=null
    private var phone:String?=null
    private var password:String?=null

    private var standard:String?=null
    private var sec:String?=null

    private var sex:String?=null
    private var dob:String?=null
    private var father:String?=null
    private var mother:String?=null
    private var country:String?=null
    private var marital:String?=null
    private var idproof:String?=null
    private var doc2:String?=null
    private var doc3:String?=null
    private var joindate:String?=null
    private var image:String?=null
    private var salary:String?=null
    private var jobtype:String?=null

    private var schoolcode:String?=null
    private var school:String?=null

    private var school_address:String?=null
    private var slogo:String?=null
    private var phone_no:String?=null
    private var phone_no2:String?=null
    private var email_id:String?=null
    private var facebook:String?=null
    private var twitter:String?=null
    private var instagram:String?=null
    private var youtube:String?=null
    private var fcmid:String?=null
    private var tracker_username:String?=null
    private var tracker_password:String?=null
    private var sms_token:String?=null

    fun getId():Int{

        return id!!
    }

    fun getName():String{

        return name!!
    }

    fun getEmail():String{

        return email!!
    }

    fun getAddress():String{

        return address!!
    }
    fun getMobile():String{

        return mobile!!
    }
    fun getPhone():String{

        return phone!!
    }
    fun getStandard():String{

        return standard!!
    }

    fun getSec():String{

        return sec!!
    }
    fun getSex():String{

        return sex!!
    }

    fun getDob():String{

        return dob!!
    }
    fun getFather():String{

        return father!!
    }
    fun getMother():String{

        return mother!!
    }

    fun getCountry():String{

        return country!!
    }

    fun getMarital():String{

        return marital!!
    }

    fun getIdproof():String{

        return idproof!!
    }

    fun getDoc2():String{

        return doc2!!
    }

    fun getDoc3():String{

        return doc3!!
    }

    fun getJoindate():String{

        return joindate!!
    }

    fun getImage():String{

        return image!!
    }

    fun getSalary():String{

        return salary!!
    }
    fun getJobtype():String{

        return jobtype!!
    }



    fun getSchool():String{

        return school!!
    }

    fun getSchoolcode():String{

        return schoolcode!!
    }

    fun getSchool_address():String{

        return school_address!!
    }

    fun getSlogo():String{

        return slogo!!
    }

    fun getPhone_no():String{

        return phone_no!!
    }

    fun getPhone_no2():String{

        return phone_no2!!
    }

    fun getEmail_id():String{

        return email_id!!
    }

    fun getFacebook():String{

        return facebook!!
    }

    fun getTwitter():String{

        return twitter!!
    }

    fun getInstagram():String{

        return instagram!!
    }
    fun getYoutube():String{

        return youtube!!
    }

    fun getFcmid():String{

        return fcmid!!
    }

    fun getTracker_username():String{

        return tracker_username!!
    }

    fun getTracker_password():String{

        return tracker_password!!
    }
    fun getSms_token():String{

        return sms_token!!
    }




    fun setStandard(standard:String){
        this.standard=standard
    }

    fun setSec(sec:String){
        this.sec=sec
    }

    fun setEmail(email:String){

        this.email=email
    }

    fun setPassword(password:String){

        this.password=password
    }



}
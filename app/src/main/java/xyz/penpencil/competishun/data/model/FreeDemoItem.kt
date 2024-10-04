package xyz.penpencil.competishun.data.model

data class FreeDemoItem(
    val id:String,
    val playIcon:Int,
    var titleDemo:String,
    var timeDemo:String,
    var fileUrl: String,
    var fileType: String,
    var videoCount:String,
    var pdfCount:String,
    var folderCount:String

)

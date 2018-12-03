package production.toth.attila.homesecurityinkotlin.models

import okhttp3.MultipartBody
import java.io.File

data class UploadModel(val imageFile: File,
                       val isNotifiableByEmail: Boolean)
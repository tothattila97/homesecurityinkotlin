package production.toth.attila.homesecurityinkotlin.models

import java.io.File

data class UploadModel(val image: File,
                       val isNotifiableByEmail: Boolean)
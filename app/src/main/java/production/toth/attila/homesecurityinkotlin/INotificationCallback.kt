package production.toth.attila.homesecurityinkotlin

interface INotificationCallback {
    fun playRingtone()
    //fun sendEmailNotification()
    fun sendSmsNotification()
}
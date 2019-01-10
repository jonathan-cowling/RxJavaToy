package tk.jonathancowling.rxjavatoy.service.mapper

class ProfileMapper {
    fun apply(profileStringId: String) = Integer.parseInt(profileStringId)
}
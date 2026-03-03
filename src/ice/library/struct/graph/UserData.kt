package ice.library.struct.graph

open class UserData<D : UserData<D>> {
    open fun maintain(left: D?, right: D?) {}
}
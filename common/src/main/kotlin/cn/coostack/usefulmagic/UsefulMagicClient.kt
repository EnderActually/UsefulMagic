package cn.coostack.usefulmagic

import cn.coostack.cooparticlesapi.CooParticlesAPIClient
import cn.coostack.cooparticlesapi.renderer.client.ClientRenderPipelineManager
import cn.coostack.usefulmagic.renderer.UsefulMagicShaderPipelines
import cn.coostack.usefulmagic.gui.friend.FriendManagerScreen
import cn.coostack.usefulmagic.utils.ParticleOption
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object UsefulMagicClient {
    lateinit var friendUIBinding: KeyMapping
    lateinit var toggleManaBarBinding: KeyMapping
    var manaBarVisible: Boolean = true
    private var renderEntitiesInitialized = false
    private var renderEntitiesPendingInit = false

    @JvmStatic
    val option: Int
        get() = ParticleOption.getParticleCounts()

    fun init() {
        renderEntitiesPendingInit = true
    }

    fun loadKeyBindings(friendKey: KeyMapping, toggleManaBarKey: KeyMapping) {
        friendUIBinding = friendKey
        toggleManaBarBinding = toggleManaBarKey
    }

    fun tickClient() {
        ensureRenderEntitiesInitialized()
        val minecraft = Minecraft.getInstance()
        if (toggleManaBarBinding.consumeClick()) {
            manaBarVisible = !manaBarVisible
            val messageKey = if (manaBarVisible) {
                "message.usefulmagic.mana_bar.enabled"
            } else {
                "message.usefulmagic.mana_bar.disabled"
            }
            minecraft.player?.displayClientMessage(Component.translatable(messageKey), false)
        }
        if (friendUIBinding.isDown) {
            minecraft.setScreen(FriendManagerScreen())
        }
    }

    fun shouldShowManaBar(): Boolean {
        return manaBarVisible
    }

    private fun ensureRenderEntitiesInitialized() {
        if (!renderEntitiesPendingInit || renderEntitiesInitialized) {
            return
        }
        val minecraft = Minecraft.getInstance()
        val renderTarget = minecraft.mainRenderTarget
        if (minecraft.window == null || renderTarget == null) {
            return
        }
        if (
            renderTarget.width <= 0 ||
            renderTarget.height <= 0 ||
            renderTarget.colorTextureId <= 0 ||
            renderTarget.depthTextureId <= 0
        ) {
            return
        }
        initRenderEntities(renderTarget.width, renderTarget.height)
    }

    private fun initRenderEntities(width: Int, height: Int) {
        if (renderEntitiesInitialized) {
            return
        }
        CooParticlesAPIClient.syncRenderBackend()
        ClientRenderPipelineManager.resizeTo(width, height)
        CooParticlesAPIClient.initShaderPrograms()
        UsefulMagicShaderPipelines.init()
        renderEntitiesInitialized = true
        renderEntitiesPendingInit = false
    }
}

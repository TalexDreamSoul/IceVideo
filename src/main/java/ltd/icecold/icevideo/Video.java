package ltd.icecold.icevideo;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoEngineVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngine;
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.videoengine.VideoEngineCallbackAdapter;

import java.util.concurrent.Semaphore;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
/**
 * @author ice_cold
 * @date Create in 13:38 2020/7/4
 */
public class Video {

    private final VideoEngineCallback videoEngineCallback;
    private final Semaphore contextSemaphore = new Semaphore(0);
    private final MediaPlayerFactory mediaPlayerFactory;
    private final EmbeddedMediaPlayer mediaPlayer;
    private VideoEngineVideoSurface videoSurface;
    private long window;

    public Video() {
        //NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"C:\\Users\\gd\\Desktop\\vlc");
        this.videoEngineCallback =  new VideoEngineHandler();
        this.window = Minecraft.getInstance().getMainWindow().getHandle();
        this.mediaPlayerFactory = new MediaPlayerFactory();
        this.mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        this.videoSurface = mediaPlayerFactory.videoSurfaces().newVideoSurface(VideoEngine.libvlc_video_engine_opengl, videoEngineCallback);

        this.mediaPlayer.videoSurface().set(videoSurface);

    }

    public void playVideo(String mrl) {
        Minecraft.getInstance().mouseHelper.ungrabMouse();
        mediaPlayer.media().play(mrl);
        loop();
        mediaPlayer.release();
        mediaPlayerFactory.release();
        glfwFreeCallbacks(window);
    }


    private void loop() {
        GL.createCapabilities();
        glfwMakeContextCurrent(0L);
        contextSemaphore.release();
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            try {
                Thread.sleep(1000 / 60);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class VideoEngineHandler extends VideoEngineCallbackAdapter {
        @Override
        public long onGetProcAddress(Pointer opaque, String functionName) {
            return glfwGetProcAddress(functionName);
        }

        @Override
        public boolean onMakeCurrent(Pointer opaque, boolean enter) {
            if (enter) {
                try {
                    contextSemaphore.acquire();
                    glfwMakeContextCurrent(window);
                } catch (Exception e) {
                    glfwMakeContextCurrent(0L);
                    contextSemaphore.release();
                    return false;
                }
            } else {
                try {
                    glfwMakeContextCurrent(0L);
                } finally {
                    contextSemaphore.release();
                }
            }
            return true;
        }

        @Override
        public void onSwap(Pointer opaque) {
            glfwSwapBuffers(window);
        }

        @Override
        public boolean onUpdateOutput(Pointer opaque, int width, int height) {
            glfwSetWindowAspectRatio(window, width, height);
            return true;
        }

        @Override
        public void onCleanup(Pointer opaque) {
            Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().displayGuiScreen(null));
            //Minecraft.getInstance().gameRenderer.updateCameraAndRender(Minecraft.getInstance().getRenderPartialTicks(),Util.nanoTime(),true);
            //Minecraft.getInstance().gameRenderer.tick();
            //Minecraft.getInstance().mouseHelper.grabMouse();
        }
    }
}

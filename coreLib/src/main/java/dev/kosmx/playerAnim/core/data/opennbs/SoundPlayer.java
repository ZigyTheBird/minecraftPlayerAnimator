package dev.kosmx.playerAnim.core.data.opennbs;

import dev.kosmx.playerAnim.core.data.opennbs.format.Layer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Plays {@link NBS} objects
 * It will need to be played on ClientSide
 */
public class SoundPlayer {
    final NBS song;
    final float songPerMCTick;
    int mcTick = 0; //MC tick (20 tps) not the song's custom
    int soundTick = -1; //MCTick * (song tickspeed/MCTickspeed)
    boolean isPlaying = true; //set false, when stopped. Newer set to true after stopped, instead create a new player.
    //To play the song in some interesting ways
    final Consumer<Layer.Note> playSound;

    public SoundPlayer(NBS song, Consumer<Layer.Note> soundPlayer, int tickToBegin) {
        this.song = song;
        this.songPerMCTick = ((float) song.header.Song_tempo) / 2000f;
        this.playSound = soundPlayer;
        this.mcTick = tickToBegin;
    }

    public void tick(){
        int newSongTick = (int) (mcTick++ * songPerMCTick);
        if(newSongTick > song.header.Loop_start_tick && song.header.Loop_on_off()){
            if(song.header.Max_loop_count != 0){
                int loop = song.header.Max_loop_count & 0xFF; //turn it into an unsigned byte
                if((newSongTick - song.header.Loop_start_tick) / (song.getLength() - song.header.Loop_start_tick) > loop){
                    this.stop();
                    return;
                }
            }
            newSongTick = (newSongTick - song.header.Loop_start_tick) % (song.getLength() - song.header.Loop_start_tick) + song.header.Loop_start_tick;
        }
        else if(newSongTick > song.getLength()){
            this.stop();
            return;
        }
        if(newSongTick == this.soundTick){
            return; //Nothing has happened, can continue;
        }
        List<Layer.Note> notesToPlay = this.song.getNotesUntilTick(this.soundTick, newSongTick);
        //MinecraftClient.getInstance().world.playSoundFromEntity();
        notesToPlay.forEach(this.playSound);

        this.soundTick = newSongTick;

    }

    public void stop(){
        this.isPlaying = false;
    }

    //My favorite one :D
    public static boolean isPlayingSong(@Nullable SoundPlayer player){
        return player != null && player.isPlaying;
    }

    //TODO put it somewhere else where MC code is available and DELETE ME
    /*
    public static Instrument getInstrumentFromCode(byte code){

        //That is more efficient than a switch case...
        Instrument[] instruments = {Instrument.HARP, Instrument.BASS, Instrument.BASEDRUM, Instrument.SNARE, Instrument.HAT,
                Instrument.GUITAR, Instrument.FLUTE, Instrument.BELL, Instrument.CHIME, Instrument.XYLOPHONE,Instrument.IRON_XYLOPHONE,
                Instrument.COW_BELL, Instrument.DIDGERIDOO, Instrument.BIT, Instrument.BANJO, Instrument.PLING};

        if(code >= 0 && code < instruments.length){
            return instruments[code];
        }
        return Instrument.HARP; //I don't want to crash here
    }

     */
}

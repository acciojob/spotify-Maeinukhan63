package com.driver;

import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        spotifyRepository.saveUser(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        spotifyRepository.saveArtist(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = spotifyRepository.findArtistByName(artistName);
        if (artist == null) {
            artist = createArtist(artistName);
        }
        Album album = new Album(title, artist);
        spotifyRepository.saveAlbum(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = spotifyRepository.findAlbumByTitle(albumName);
        if (album == null) {
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, album, length);
        spotifyRepository.saveSong(song);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = spotifyRepository.findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title, user);
        List<Song> allSongs = spotifyRepository.findAllSongs();
        for (Song song : allSongs) {
            if (song.getLength() == length) {
                playlist.addSong(song);
            }
        }
        spotifyRepository.savePlaylist(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = spotifyRepository.findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title, user);
        for (String songTitle : songTitles) {
            Song song = spotifyRepository.findSongByTitle(songTitle);
            if (song != null) {
                playlist.addSong(song);
            }
        }
        spotifyRepository.savePlaylist(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = spotifyRepository.findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = spotifyRepository.findPlaylistByTitle(playlistTitle);
        if (playlist == null) {
            throw new Exception("Playlist does not exist");
        }
        playlist.addListener(user);
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = spotifyRepository.findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Song song = spotifyRepository.findSongByTitle(songTitle);
        if (song == null) {
            throw new Exception("Song does not exist");
        }
        if (!spotifyRepository.userHasLikedSong(user, song)) {
            song.like();
            song.getAlbum().getArtist().like();
            spotifyRepository.saveUserLike(user, song);
        }
        return song;
    }

    public String mostPopularArtist() {
        Artist mostPopular = null;
        int maxLikes = 0;
        List<Artist> allArtists = spotifyRepository.findAllArtists();
        for (Artist artist : allArtists) {
            if (artist.getLikes() > maxLikes) {
                maxLikes = artist.getLikes();
                mostPopular = artist;
            }
        }
        return mostPopular != null ? mostPopular.getName() : null;
    }

    public String mostPopularSong() {
        Song mostPopular = null;
        int maxLikes = 0;
        List<Song> allSongs = spotifyRepository.findAllSongs();
        for (Song song : allSongs) {
            if (song.getLikes() > maxLikes) {
                maxLikes = song.getLikes();
                mostPopular = song;
            }
        }
        return mostPopular != null ? mostPopular.getTitle() : null;
    }
}

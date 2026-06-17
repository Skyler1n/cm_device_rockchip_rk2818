package android.rk.RockAudioPlayer;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IAudioPlaybackService extends IInterface {
    long duration() throws RemoteException;

    void enqueue(int[] iArr, int i) throws RemoteException;

    int getAlbumId() throws RemoteException;

    String getAlbumName() throws RemoteException;

    int getArtistId() throws RemoteException;

    String getArtistName() throws RemoteException;

    int getAudioId() throws RemoteException;

    String getDirPath() throws RemoteException;

    String getDisplayName() throws RemoteException;

    String getDisplayname() throws RemoteException;

    long getDuration() throws RemoteException;

    int getGenrelId() throws RemoteException;

    String getGenrelName(int i) throws RemoteException;

    int getMediaMountedCount() throws RemoteException;

    String getPath() throws RemoteException;

    int[] getQueue() throws RemoteException;

    int getQueuePosition() throws RemoteException;

    int getRepeatMode() throws RemoteException;

    int getShuffleMode() throws RemoteException;

    String getTrackName() throws RemoteException;

    String getTracknameBack() throws RemoteException;

    int getbitrate() throws RemoteException;

    boolean isPlaying() throws RemoteException;

    void moveQueueItem(int i, int i2) throws RemoteException;

    void next() throws RemoteException;

    void open(int[] iArr, int i) throws RemoteException;

    void openfile(String str) throws RemoteException;

    void openfileAsync(String str) throws RemoteException;

    void opennext() throws RemoteException;

    void pause() throws RemoteException;

    void play() throws RemoteException;

    long position() throws RemoteException;

    void prev() throws RemoteException;

    int removeTrack(int i) throws RemoteException;

    int removeTracks(int i, int i2) throws RemoteException;

    long seek(long j) throws RemoteException;

    void setDisplayname(String str) throws RemoteException;

    void setQueuePosition(int i) throws RemoteException;

    void setRepeatMode(int i) throws RemoteException;

    void setShuffleMode(int i) throws RemoteException;

    void setTracknameBack(String str) throws RemoteException;

    void stop() throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAudioPlaybackService {
        private static final String DESCRIPTOR = "android.rk.RockAudioPlayer.IAudioPlaybackService";
        static final int TRANSACTION_duration = 12;
        static final int TRANSACTION_enqueue = 23;
        static final int TRANSACTION_getAlbumId = 17;
        static final int TRANSACTION_getAlbumName = 16;
        static final int TRANSACTION_getArtistId = 20;
        static final int TRANSACTION_getArtistName = 18;
        static final int TRANSACTION_getAudioId = 29;
        static final int TRANSACTION_getDirPath = 28;
        static final int TRANSACTION_getDisplayName = 19;
        static final int TRANSACTION_getDisplayname = 42;
        static final int TRANSACTION_getDuration = 38;
        static final int TRANSACTION_getGenrelId = 22;
        static final int TRANSACTION_getGenrelName = 21;
        static final int TRANSACTION_getMediaMountedCount = 37;
        static final int TRANSACTION_getPath = 27;
        static final int TRANSACTION_getQueue = 24;
        static final int TRANSACTION_getQueuePosition = 4;
        static final int TRANSACTION_getRepeatMode = 35;
        static final int TRANSACTION_getShuffleMode = 31;
        static final int TRANSACTION_getTrackName = 15;
        static final int TRANSACTION_getTracknameBack = 40;
        static final int TRANSACTION_getbitrate = 36;
        static final int TRANSACTION_isPlaying = 5;
        static final int TRANSACTION_moveQueueItem = 25;
        static final int TRANSACTION_next = 10;
        static final int TRANSACTION_open = 3;
        static final int TRANSACTION_openfile = 1;
        static final int TRANSACTION_openfileAsync = 2;
        static final int TRANSACTION_opennext = 11;
        static final int TRANSACTION_pause = 7;
        static final int TRANSACTION_play = 8;
        static final int TRANSACTION_position = 13;
        static final int TRANSACTION_prev = 9;
        static final int TRANSACTION_removeTrack = 33;
        static final int TRANSACTION_removeTracks = 32;
        static final int TRANSACTION_seek = 14;
        static final int TRANSACTION_setDisplayname = 41;
        static final int TRANSACTION_setQueuePosition = 26;
        static final int TRANSACTION_setRepeatMode = 34;
        static final int TRANSACTION_setShuffleMode = 30;
        static final int TRANSACTION_setTracknameBack = 39;
        static final int TRANSACTION_stop = 6;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAudioPlaybackService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAudioPlaybackService)) {
                return new Proxy(obj);
            }
            return (IAudioPlaybackService) iin;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    openfile(_arg0);
                    reply.writeNoException();
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    openfileAsync(_arg02);
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg03 = data.createIntArray();
                    int _arg1 = data.readInt();
                    open(_arg03, _arg1);
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = getQueuePosition();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result2 = isPlaying();
                    reply.writeNoException();
                    if (_result2) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    reply.writeInt(i);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    stop();
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    pause();
                    reply.writeNoException();
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    play();
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    prev();
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    next();
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    opennext();
                    reply.writeNoException();
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    long _result3 = duration();
                    reply.writeNoException();
                    reply.writeLong(_result3);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    long _result4 = position();
                    reply.writeNoException();
                    reply.writeLong(_result4);
                    return true;
                case TRANSACTION_seek /* 14 */:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg04 = data.readLong();
                    long _result5 = seek(_arg04);
                    reply.writeNoException();
                    reply.writeLong(_result5);
                    return true;
                case TRANSACTION_getTrackName /* 15 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result6 = getTrackName();
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case TRANSACTION_getAlbumName /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result7 = getAlbumName();
                    reply.writeNoException();
                    reply.writeString(_result7);
                    return true;
                case TRANSACTION_getAlbumId /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = getAlbumId();
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case TRANSACTION_getArtistName /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result9 = getArtistName();
                    reply.writeNoException();
                    reply.writeString(_result9);
                    return true;
                case TRANSACTION_getDisplayName /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result10 = getDisplayName();
                    reply.writeNoException();
                    reply.writeString(_result10);
                    return true;
                case TRANSACTION_getArtistId /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = getArtistId();
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case TRANSACTION_getGenrelName /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _result12 = getGenrelName(_arg05);
                    reply.writeNoException();
                    reply.writeString(_result12);
                    return true;
                case TRANSACTION_getGenrelId /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result13 = getGenrelId();
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    return true;
                case TRANSACTION_enqueue /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _arg06 = data.createIntArray();
                    int _arg12 = data.readInt();
                    enqueue(_arg06, _arg12);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getQueue /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result14 = getQueue();
                    reply.writeNoException();
                    reply.writeIntArray(_result14);
                    return true;
                case TRANSACTION_moveQueueItem /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg13 = data.readInt();
                    moveQueueItem(_arg07, _arg13);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setQueuePosition /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    setQueuePosition(_arg08);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getPath /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result15 = getPath();
                    reply.writeNoException();
                    reply.writeString(_result15);
                    return true;
                case TRANSACTION_getDirPath /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result16 = getDirPath();
                    reply.writeNoException();
                    reply.writeString(_result16);
                    return true;
                case TRANSACTION_getAudioId /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result17 = getAudioId();
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    return true;
                case TRANSACTION_setShuffleMode /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    setShuffleMode(_arg09);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getShuffleMode /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result18 = getShuffleMode();
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    return true;
                case TRANSACTION_removeTracks /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg14 = data.readInt();
                    int _result19 = removeTracks(_arg010, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    return true;
                case TRANSACTION_removeTrack /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _result20 = removeTrack(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    return true;
                case TRANSACTION_setRepeatMode /* 34 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    setRepeatMode(_arg012);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getRepeatMode /* 35 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result21 = getRepeatMode();
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    return true;
                case TRANSACTION_getbitrate /* 36 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result22 = getbitrate();
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    return true;
                case TRANSACTION_getMediaMountedCount /* 37 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result23 = getMediaMountedCount();
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    return true;
                case TRANSACTION_getDuration /* 38 */:
                    data.enforceInterface(DESCRIPTOR);
                    long _result24 = getDuration();
                    reply.writeNoException();
                    reply.writeLong(_result24);
                    return true;
                case TRANSACTION_setTracknameBack /* 39 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    setTracknameBack(_arg013);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getTracknameBack /* 40 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result25 = getTracknameBack();
                    reply.writeNoException();
                    reply.writeString(_result25);
                    return true;
                case TRANSACTION_setDisplayname /* 41 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    setDisplayname(_arg014);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getDisplayname /* 42 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result26 = getDisplayname();
                    reply.writeNoException();
                    reply.writeString(_result26);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAudioPlaybackService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void openfile(String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void openfileAsync(String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void open(int[] list, int position) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(list);
                    _data.writeInt(position);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getQueuePosition() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public boolean isPlaying() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readInt() != 0;
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void pause() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void play() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void prev() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void next() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void opennext() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public long duration() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public long position() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public long seek(long pos) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(pos);
                    this.mRemote.transact(Stub.TRANSACTION_seek, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getTrackName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTrackName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getAlbumName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAlbumName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getAlbumId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAlbumId, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getArtistName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getArtistName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getDisplayName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDisplayName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getArtistId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getArtistId, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getGenrelName(int songid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(songid);
                    this.mRemote.transact(Stub.TRANSACTION_getGenrelName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getGenrelId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getGenrelId, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void enqueue(int[] list, int action) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(list);
                    _data.writeInt(action);
                    this.mRemote.transact(Stub.TRANSACTION_enqueue, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int[] getQueue() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getQueue, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void moveQueueItem(int from, int to) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(from);
                    _data.writeInt(to);
                    this.mRemote.transact(Stub.TRANSACTION_moveQueueItem, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void setQueuePosition(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    this.mRemote.transact(Stub.TRANSACTION_setQueuePosition, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getPath() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPath, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getDirPath() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDirPath, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getAudioId() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAudioId, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void setShuffleMode(int shufflemode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(shufflemode);
                    this.mRemote.transact(Stub.TRANSACTION_setShuffleMode, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getShuffleMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getShuffleMode, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int removeTracks(int first, int last) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(first);
                    _data.writeInt(last);
                    this.mRemote.transact(Stub.TRANSACTION_removeTracks, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int removeTrack(int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(Stub.TRANSACTION_removeTrack, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void setRepeatMode(int repeatmode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(repeatmode);
                    this.mRemote.transact(Stub.TRANSACTION_setRepeatMode, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getRepeatMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getRepeatMode, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getbitrate() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getbitrate, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public int getMediaMountedCount() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMediaMountedCount, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public long getDuration() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDuration, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void setTracknameBack(String str) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_setTracknameBack, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getTracknameBack() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTracknameBack, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public void setDisplayname(String str) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(str);
                    this.mRemote.transact(Stub.TRANSACTION_setDisplayname, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
            public String getDisplayname() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDisplayname, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
    }
}

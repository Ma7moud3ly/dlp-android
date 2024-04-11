from yt_dlp import YoutubeDL, DownloadError, version
import ctypes
import threading
import inspect
import ctypes


# Handle download process in a seprate thread

thread1 = None

# Kill the runnig thread
# https://github.com/chaquo/chaquopy/issues/58


def _async_raise(tid, exctype):
    tid = ctypes.c_long(tid)
    if not inspect.isclass(exctype):
        exctype = type(exctype)
    res = ctypes.pythonapi.PyThreadState_SetAsyncExc(
        tid, ctypes.py_object(exctype))
    if res == 0:
        raise ValueError("invalid thread id")
    elif res != 1:
        ctypes.pythonapi.PyThreadState_SetAsyncExc(tid, None)
        raise SystemError("PyThreadState_SetAsyncExc failed")


def stop_thread(thread):
    _async_raise(thread.ident, SystemExit)

# Public callback methods being overrided by Android code
# to observe download progress and error messages


def download_progress(downloaded, total, percent):
    pass


def download_complete():
    pass


def download_error(msg):
    pass


# Get yt-dlp version
def dlp_version():
    return "yt-dlp %s %s" % (version.CHANNEL, version._pkg_version)


def dlp_update():
    pass

#########################
# YTDL functions
#########################


allowed_codecs = ["mp4", "webm", "mp3", "mkv", "avi", "m4a"]

# Check if video title is a valid file name to save file with.


def isValidName(filename):
    try:
        x = open(filename, "w")
        x.close()
    except Exception as e:
        print(e)
        return False
    return True

# Download progress callback


def my_hook(d):
    if d['status'] == 'downloading':
        downloaded = int(d['downloaded_bytes']
                         if 'downloaded_bytes' in d else '0')
        total = int(d['total_bytes'] if 'total_bytes' in d else '0')
        if total == 0:
            download_progress(downloaded, 0, 0)
        else:
            percent = round(downloaded/total*100, 1)
            download_progress(downloaded, total, percent)

# Get video info (title,thumbnail,description and formats)


def getInfo(url):
    options = {'format': 'best'}
    with YoutubeDL(options) as ydl:
        info = ydl.extract_info(url, download=False)
        meta = info.get('formats', [info])
        title = info['title'] if 'title' in info else ''
        thumbnail = info['thumbnail'] if 'thumbnail' in info else ''
        description = info['description'] if 'description' in info else ''
        if description == None:
            description = ""
        duration = info['duration'] if 'duration' in info else 0
        formats_dict = {}
        for m in meta:
            ext = m['ext'] if "ext" in m else ""
            if ext == None:
                ext = ""
            resolution = m['resolution'] if "resolution" in m else ""
            if resolution == None:
                resolution = ""
            file_size = m['filesize'] if "filesize" in m else ""
            format_id = m["format_id"]
            key = format_id+ext+resolution
            if ext not in allowed_codecs:
                continue
            format = {
                "format_id": format_id,
                "format_note": m['format_note'] if "format_note" in m else "",
                "file_size": file_size,
                "media_link": m['url'] if "url" in m else "",
                "resolution": resolution,
                "ext": ext
            }

            if key not in formats_dict:
                formats_dict[key] = format

        formats = list(formats_dict.values())
        formats.reverse()
        info = {
            "url": url,
            "title": title,
            "thumbnail": thumbnail,
            "description": description,
            "duration": int(duration),
            "formats": formats
        }

        return info

# Download a video with ydl-dl


def _download(url, format_id, path, title):
    valid = isValidName(path+"/"+title)
    print("valid name", valid)
    outtmpl = "/%(title)s.%(ext)s" if valid else "/%(id)s.%(ext)s"
    options = {
        "progress_hooks": [my_hook],
        'warnings': 'no-warnings',
        "outtmpl": path+outtmpl,
        'noplaylist': True,
        'listformats': False,
    }
    if format_id != '':
        options['format'] = format_id

    with YoutubeDL(options) as ydl:
        try:
            ydl.download([url])
            download_complete()
        except DownloadError as e:
            download_error(e)


# start download in a new thread
def startDownload(url, format_id="bestvideo", path="", title=""):
    global thread1
    thread1 = threading.Thread(
        target=_download, args=(url, format_id, path, title))
    thread1.start()
    print("thread1 start")


# stop download task
def stopDownload():
    stop_thread(thread1)

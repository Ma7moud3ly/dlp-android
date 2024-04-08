from yt_dlp import YoutubeDL
import ctypes
import threading
import inspect
import ctypes
from time import sleep

# Handle download process in seprate thread
thread1 = None


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


def download_progress(downloaded, total, percent):
    print("-----------downloading "+"%s : %s -  %s" %
          (downloaded, total, percent))


def download_complete():
    pass

# YTDL functions


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


def getInfo(url):
    with YoutubeDL() as ydl:
        info = ydl.extract_info(url, download=False)
        meta = info.get('formats', [info])
        title = info['title'] if 'title' in info else ''
        thumbnail = info['thumbnail'] if 'thumbnail' in info else ''
        description = info['description'] if 'description' in info else ''
        duration = info['duration'] if 'duration' in info else 0
        formats = []
        for m in meta:
            format = {
                "format_id": m["format_id"],
                "format_note": m['format_note'] if "format_note" in m else "",
                "file_size": m['filesize'] if "filesize" in m else "",
                "media_link": m['url'] if "url" in m else "",
                "resolution": m['resolution'] if "resolution" in m else "",
                "ext": m['ext'] if "ext" in m else ""
            }
            formats.append(format)
        else:
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


def _download(url, format, path):
    options = {
        "progress_hooks": [my_hook],
        'format': format,
        'warnings': 'no-warnings',
        "outtmpl": path+"/%(title)s.%(ext)s",
        'noplaylist': True,
        'listformats': False,
    }
    ydl = YoutubeDL(options)
    ydl.download([url])
    download_complete()


# start download in seprate thread
def startDownload(url, format="bestvideo", path=""):
    global thread1
    thread1 = threading.Thread(target=_download, args=(url, format, path))
    thread1.start()
    print("thread1 start")


# stop downloading thread
def stopDownload():
    stop_thread(thread1)


if __name__ == '__main__':
    url = "https://www.youtube.com/watch?v=BaW_jenozKc"
    # url = "https://www.facebook.com/watch/?v=396440209817545"
    # info = getInfo(url)
    # del info['formats']
    # print(info)
    # download(url, path="F://")
    startDownload(url, path="F://")
    sleep(5)
    stopDownload()

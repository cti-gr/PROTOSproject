from distutils.core import setup
import py2exe

setup(
	name = 'FirelogSvc',
	description = 'Firelog Service Launcher',
	version = '2.2',
	service = [{'modules':["ServiceLauncherWin"], 'cmdline':'pywin32'}],
	zipfile = None,
	options = {
		"py2exe":{"packages":"encodings",
			"includes" : "win32com, win32service, win32serviceutil, win32event",
			"optimize" : '2'
			},
	}
)

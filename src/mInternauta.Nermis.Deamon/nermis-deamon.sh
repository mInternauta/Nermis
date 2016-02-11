#! /bin/sh
#
# -----------------------------------
# Initscript for the Nermis
# -----------------------------------
#

set -e
PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
DESC="Nermis"
NAME=nermis
INSTALL_PATH="/usr/local/nermis/"
DAEMON="/usr/bin/java -jar nermis.jar --service"
PIDFILE=/var/run/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME

# Gracefully exit if the package has been removed.
test -x /usr/bin/java || exit 0

# ---------------------------------------
# Function that starts the daemon/service
# ---------------------------------------
d_start()
{
su -p -s /bin/sh "cd $INSTALL_PATH & $DAEMON &> /dev/null & echo $!" > $PIDFILE
}

# --------------------------------------
# Function that stops the daemon/service
# --------------------------------------
d_stop()
{
start-stop-daemon --stop --quiet --pidfile $PIDFILE
}

case "$1" in
start)
echo -n "Starting $DESC: $NAME"
d_start
echo "."
;;
stop)
echo -n "Stopping $DESC: $NAME"
d_stop
echo "."
;;
restart|force-reload)
echo -n "Restarting $DESC: $NAME"
d_stop
sleep 1
d_start
echo "."
;;
*)
echo "Usage: $SCRIPTNAME {start|stop|restart|force-reload}" >&2
exit 1
;;
esac

exit 0
function readJson(callback)
{
    var localFile = new XMLHttpRequest();
    localFile.open("GET", 'local_data.json?' + (new Date()).getTime(), true);
    localFile.onreadystatechange = function ()
    {
        if(localFile.readyState === 4)
        {
            if(localFile.status === 200 || localFile.status == 0)
            {
                callback(localFile.responseText);                
            }
        }
    }
    localFile.send(null);
}

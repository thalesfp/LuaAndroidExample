function find_binary(place, binary) 
    local f=io.open(place..binary,"r")
    if f~=nil then io.close(f) return true else return false end
end

function result()
    local binary = "su"
    local result = "false"

    local places = { "/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", 
                     "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", 
                     "/data/local/" }

    for i, place in pairs(places) do
        if find_binary(place, binary) then
            result = "true"
        end
    end

    return result
end

name = "isRooted"

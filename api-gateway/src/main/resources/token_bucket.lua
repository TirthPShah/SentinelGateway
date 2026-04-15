local tokenKey = KEYS[1]
local timeKey = KEYS[2]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local tokens = tonumber(redis.call("GET", tokenKey))
if tokens == nil then
    tokens = capacity
end

local lastRefill = tonumber(redis.call("GET", timeKey))
if lastRefill == nil then
    lastRefill = now
end

local delta = math.max(0, now - lastRefill)
local refill = delta * refillRate
tokens = math.min(capacity, tokens + refill)

if tokens < 1 then
    return 0
end

tokens = tokens - 1

redis.call("SET", tokenKey, tokens)
redis.call("SET", timeKey, now)

return 1
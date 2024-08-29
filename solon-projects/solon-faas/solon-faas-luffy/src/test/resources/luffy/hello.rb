name = ctx.param('name')

if(!name)
    name = "world"
end

return "Hello #{name}!"
//@{method:"GET"}

let name = ctx.param("name");

if(!name){
    name = "world";
}

return name;
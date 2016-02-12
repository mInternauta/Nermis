Context = getWebContext();

writer = Context.Response:getWriter();
writer:println("Hello World!");

cData = createInstance("java.util.Date");
cStrData = cData:toString();

writer:println(cStrData);

Context.Response:setStatus(200);

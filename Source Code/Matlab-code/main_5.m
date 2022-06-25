%for data16col
load data12col
load data4col

data1=data12col;
m=size(data1,1);
n=size(data1,2);
data2=data4col(13:end,2:end);

data3=ones(m,17);
data3(:,1:13)=data1;
data3(:,14:17)=data2;

data16col=data3;

[ train_set,test_set] = partitionData( data3,0.7);
save data16col;

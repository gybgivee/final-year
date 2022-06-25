%for 4 col
load dataRain
data1=xlsread('4col'); 
m=size(data1,1);
 data2 = 2 * mat2gray(data1)-1;
 data3=toClass(dataRain,1);
 
 data4 = ones(m,5);
 data4(:,1)=data3;
 data4(:,2:5)=data2;
 
 [ train_set,test_set] = partitionData( data4,0.7);
 
 data4col=data4;
 
 save data4col
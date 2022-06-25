 %only weangkao cause starting on 50
data=xlsread('weangkao-50'); 
 m=size(data,1);
 n=size(data,2);
 newCol=m*n;
 newData=ones(size(1,newCol));
 
 col=1;
     for i=1:m
         for j=1:n
     
     newData(1,col)=data(i,j);
      col=col+1;
         end 
     end
     
  stRow=1;   
  stCol=13;
  
  newData2=ones(size(500,13));
  i2=1;
  count=0;
  while (stCol <= newCol)
      newData2(i2,1:13)=newData(1,stRow:stCol);
      i2=i2+1;
      stRow=stRow+1;
      stCol=stCol+1;
      count=count+1;
  end
  newData3=newData2(1:125,:);
 [ data3 ] = toClass( newData3,13);
 data4 = 2 * mat2gray(data3(:,1:12))-1;
 data5=ones(size(newData3));
 data5(:,2:13)=data4;
 data5(:,1)=data3(:,13);
 
 [ train_set,test_set] = partitionData( data5,0.7);

        
    
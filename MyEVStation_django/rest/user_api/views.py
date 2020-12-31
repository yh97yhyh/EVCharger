from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import UserSerializer
from rest_framework import status
from .models import User

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter

class UserQuerySet(ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer

    def get_queryset(self, query):
        queryset = super().get_queryset()
        queryset = queryset.filter(addr=query)
        return queryset

# Create your views here.

class UserView(APIView):
    def post(self, request):
        user_serializer = UserSerializer(data=request.data)
        if user_serializer.is_valid():
            user_serializer.save()
            return Response({'result':'success', 'data':user_serializer.data}, 
                            status=status.HTTP_201_CREATED)
        else:
            return Response({'result':'fail', 'data':user_serializer.errors},
                            status=status.HTTP_400_BAD_REQUEST)

    def get(self, request, **kwargs):
        if kwargs.get('u_id') is None:
            if request.GET.get('query') is None:
                user_queryset = User.objects.all()
            else:
                query = request.GET.get('query')
                user_queryset = UserQuerySet().get_queryset(query)

            user_queryset_serializer = UserSerializer(user_queryset, many=True)
            return Response(user_queryset_serializer.data, status=status.HTTP_200_OK)
        else:
            u_id = kwargs.get('u_id')
            user_one_serializer = UserSerializer(User.objects.get(id=u_id))
            return Response(user_one_serializer.data, status=status.HTTP_200_OK)

    def put(self, request, **kwargs):
        if kwargs.get('u_id') is None:
            return Response('None', status=status.HTTP_400_BAD_REQUEST)
        else:
            u_id = kwargs.get('u_id')
            user_obj = User.objects.get(id=u_id)
            user_put_serailizer = UserSerializer(user_obj, data=request.data)
            if user_put_serailizer.is_valid():
                user_put_serailizer.save()
                return Response(user_put_serailizer.data, status=status.HTTP_200_OK)
            else:
                return Response(user_put_serailizer.data, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, **kwargs):
        if kwargs.get('u_id') is None:
            return Response('None', status=status.HTTP_400_BAD_REQUEST)
        else:
            u_id = kwargs.get('u_id')
            user_obj = User.objects.get(id=u_id)
            user_obj.delete()
            return Response({'result':'success'}, status=status.HTTP_200_OK)
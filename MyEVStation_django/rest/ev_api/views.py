from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import StationsSerializer
from rest_framework import status
from .models import Stations

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter

class StationsQuerySet(ModelViewSet):
    queryset = Stations.objects.all()
    serializer_class = StationsSerializer

    def get_queryset(self, query):
        queryset = super().get_queryset()
        queryset = queryset.filter(addr=query)
        return queryset


# Create your views here.
class StationsView(APIView):
    def get(self, request):
        # stations_all_serializer = StationsSerializer(Stations.objects.all(),
        #             many=True)
        # return Response({'stations':stations_all_serializer.data,
        #                     'count':Stations.objects.count()}, status=status.HTTP_200_OK)
        if request.GET.get('query') is None:
                stations_queryset = Stations.objects.all()
        else:
            query = request.GET.get('query')
            stations_queryset = StationsQuerySet().get_queryset(query)

        stations_queryset_serializer = StationsSerializer(stations_queryset, many=True)
        return Response({'stations':stations_queryset_serializer.data,
                            'count':stations_queryset.count()}, status=status.HTTP_200_OK)





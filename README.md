# embalagens-pamplona-backend-2


run docker with env and and refresh build with new code <br/>
docker compose --env-file .env.dev -f compose-dev.yaml up --build

roda a mesma build sem atualizacao de codigo<br/>
docker compose -f compose-dev.yaml up 

#atualiza a build com codigo novo mas sem definir a env (pega a env padrao do projeto)</br>
docker compose -f compose-dev.yaml up --build
